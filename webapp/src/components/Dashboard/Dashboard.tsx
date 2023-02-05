import React, { useState } from 'react';
import { Form, Input, Button,  Card, Table, Typography, Space } from 'antd';
import { Line } from 'react-chartjs-2';
import moment from 'moment';
import DatePicker from "react-datepicker";

interface Props {
  patientName: string;
  data: Array<{
    date: string;
    stepLength: number;
    doubleSupportTime: number;
    walkingSpeed: number;
    walkingAsymmetry: number;
    distanceWalked: number;
  }>;
}

const MobilityDataDashboard: React.FC<Props> = ({ patientName, data }) => {
  const [startDate, setStartDate] = useState<moment.Moment | null>(null);
  const [endDate, setEndDate] = useState<moment.Moment | null>(null);

  const handleStartDateChange = (date: Date | null, event: any) => {
    setStartDate(moment(date));
  };

  const handleEndDateChange = (date: Date | null, event: any) => {
    setEndDate(moment(date));
  };

  const filteredData = data.filter(
    (d) => (!startDate || moment(d.date).isSameOrAfter(startDate)) && (!endDate || moment(d.date).isSameOrBefore(endDate))
  );

  const stepLengthData = filteredData.map((d) => d.stepLength);
  const doubleSupportTimeData = filteredData.map((d) => d.doubleSupportTime);
  const walkingSpeedData = filteredData.map((d) => d.walkingSpeed);
  const walkingAsymmetryData = filteredData.map((d) => d.walkingAsymmetry);
  const distanceWalkedData = filteredData.map((d) => d.distanceWalked);
  const dateData = filteredData.map((d) => d.date);

  const handleExportData = () => {
    // implement logic to export data as a csv file
  };

  const handleUpdateDevice = () => {
    // implement logic to update the device used to collect data
  };

  const handleShareData = () => {
    // implement logic to share data with another caregiver
  };

  const handleRemoveRecord = () => {
    // implement logic to remove this patient's record
  };

  const chartData = {
    labels: dateData,
    datasets: [
      {
        label: 'Distance Walked',
        data: distanceWalkedData,
        backgroundColor: 'blue',
        borderColor: 'blue',
        fill: false,
      },
    ],
  };

  const columns = [
    {
      title: 'Date',
      dataIndex: 'date',
      key: 'date',
    },
    {
      title: 'Step Length',
      dataIndex: 'stepLength',
      key: 'stepLength',
    },
    {
      title: 'Double Support Time',
      dataIndex: 'doubleSupportTime',
      key: 'doubleSupportTime',
    },
    {
      title: 'Walking Speed',
      dataIndex: 'walkingSpeed',
      key: 'walkingSpeed',
    },
    {
      title: 'Walking Asymmetry',
      dataIndex: 'walkingAsymmetry',
      key: 'walkingAsymmetry',
    },
    {
      title: 'Distance Walked',
      dataIndex: 'distanceWalked',
      key: 'distanceWalked',
    },
  ];

  const tableData = filteredData.map((d, i) => ({
    key: i,
    date: d.date,
    stepLength: d.stepLength,
    doubleSupportTime: d.doubleSupportTime,
    walkingSpeed: d.walkingSpeed,
    walkingAsymmetry: d.walkingAsymmetry,
    distanceWalked: d.distanceWalked,
  }));

  return (
    <div>
      <Typography.Title level={2}>{patientName}</Typography.Title>
      <Form layout="inline">
        <Form.Item label="Start Date">
          <DatePicker onChange={handleStartDateChange} />
        </Form.Item>
        <Form.Item label="End Date">
          <DatePicker onChange={handleEndDateChange} />
        </Form.Item>
        <Form.Item>
          <Button type="primary">Filter</Button>
        </Form.Item>
      </Form>
      <Space style={{ marginTop: 16 }}>
        <Button type="primary">Export Data</Button>
        <Button type="primary">Update Device</Button>
        <Button type="primary">Share</Button>
        <Button type="primary">Remove</Button>
      </Space>
      <Space style={{ marginTop: 16 }}>
        <Card title="Step Length" style={{ width: 300 }}>
          <Typography.Text strong>Value:</Typography.Text>
          <Typography.Text type="secondary">{stepLengthData.length > 0 ? stepLengthData[stepLengthData.length - 1] : 0} cm</Typography.Text>
        </Card>
        <Card title="Double Support Time" style={{ width: 300 }}>
          <Typography.Text strong>Value:</Typography.Text>
          <Typography.Text type="secondary">{doubleSupportTimeData.length > 0 ? doubleSupportTimeData[doubleSupportTimeData.length - 1] : 0} %</Typography.Text>
        </Card>
        <Card title="Walking Speed" style={{ width: 300 }}>
          <Typography.Text strong>Value:</Typography.Text>
          <Typography.Text type="secondary">{walkingSpeedData.length > 0 ? walkingSpeedData[walkingSpeedData.length - 1] : 0} m/s</Typography.Text>
        </Card>
        <Card title="Walking Asymmetry" style={{ width: 300 }}>
          <Typography.Text strong>Value:</Typography.Text>
          <Typography.Text type="secondary">{walkingAsymmetryData.length > 0 ? walkingAsymmetryData[walkingAsymmetryData.length - 1] : 0} %</Typography.Text>
        </Card>
      </Space>
      <Space style={{ marginTop: 16 }}>
        <Line data={chartData} />
      </Space>
      <Space style={{ marginTop: 16 }}>
        <Table columns={columns} dataSource={tableData} />
      </Space>
    </div>
  );
};

export default MobilityDataDashboard;
