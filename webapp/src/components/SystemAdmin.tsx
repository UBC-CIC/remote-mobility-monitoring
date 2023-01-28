import React from "react";
import "./SystemAdmin.css";

export class SignUp extends React.Component{
    handleChange = (event : any) => {
        event.preventDefault();
        const { name, value } = event.target;
        this.setState({[name]: value});
        console.log(this.state) ;
    };

    handleSubmit = (event : any) => {};

    render() {
        return (
            <div className='wrapper'>
                <div className='form-wrapper'>
                    <h2>Sign Up</h2>
                    <form onSubmit={this.handleSubmit} noValidate >
                        <div className='username'>
                            <input type='text' name='username' placeholder="Create username" onChange={this.handleChange}/>
                        </div>
                        <div className='email'>
                            <input type='email' name='email' placeholder="Enter email" onChange={this.handleChange}/>
                        </div>
                        <div className='contact_number'>
                            <input type='contact_number' name='contact number' placeholder="Contact number" onChange={this.handleChange}/>
                        </div>
                        <div className='password'>
                            <input type='password' name='password' placeholder="Password" onChange={this.handleChange}/>
                        </div>
                        <div className='Confirm_Password'>
                            <input type='password' name='confirm password' placeholder="Confirm password" onChange={this.handleChange}/>
                        </div>             
                        <div className='submit'>
                            <button>Register</button>
                        </div>
                    </form>
                </div>
            </div>
        );
    }
}